<script lang="ts">
	import {
		DEFAULT_PAGE_SIZE,
		PrivateMarketplaceApi,
		credentials,
		is_successful,
		type ICommentDto,
		type IPage,
		type IProductDto
	} from '@/api/marketplaceApi';
	import { page } from '$app/stores';
	import {
		Button,
		Carousel,
		CarouselControl,
		CarouselIndicators,
		CarouselItem,
		Container,
		Form,
		FormGroup,
		Input,
		Label,
		Modal,
		Pagination,
		PaginationItem
	} from '@sveltestrap/sveltestrap';
	import { get } from 'svelte/store';

	const productId: string = $page.params.id;
	const privateApi = PrivateMarketplaceApi.fromLocalStorage();

	let commentRequest = {
		productId: productId,
		content: '',
		rate: 5
	};

	async function loadData() {
		const product = (await privateApi.getProduct(productId)).data as IProductDto;

		return product;
	}

	async function loadComments(page: number = 0) {
		const comments = (await privateApi.listComments(productId, page, DEFAULT_PAGE_SIZE)).data;
		return comments as IPage<ICommentDto>;
	}

	let activeIndex = 0;
	let loadDataPromise = loadData();
	let loadCommentsPromise = loadComments();
	let commentsPage = 0;
	let modalOpened = false;

	let orderQuantity = 1;
 
</script>

<section>
	<Container fluid class="rounded-3 shadow-lg border">
		{#await loadDataPromise then product}
			<h2 class="product-title">{product.caption}</h2>
			<Carousel class="ms-3 mt-3 m-5 text-center" items={product.images} bind:activeIndex>
				<CarouselIndicators bind:activeIndex items={product.images} />
				<div class="carousel-inner">
					{#each product.images as img, index}
						<CarouselItem bind:activeIndex itemIndex={index}>
							<img src={img} style="max-width: 35vw; max-height: 35vh" alt="{img} {index + 1}" />
						</CarouselItem>
					{/each}
				</div>

				<CarouselControl
					class="text-bg-dark"
					direction="prev"
					bind:activeIndex
					items={product.images}
				/>

				<CarouselControl
					class="text-bg-dark"
					direction="next"
					bind:activeIndex
					items={product.images}
				/>
			</Carousel>

			<p><strong>Description:</strong> {product.description}</p>
			{#if product.price != product.actualPrice}
				<s><p><strong>Price:</strong> ${product.price}</p></s>
				<p class="text-success"><strong>Current price:</strong> ${product.actualPrice}</p>
			{:else}
				<p><strong>Price:</strong> ${product.actualPrice}</p>
			{/if}
			<p><strong>Quantity:</strong> {product.availableQuantity}</p>
			<p><strong>Rate:</strong>{product.rate}</p>
			<div class="border rounded-3 pt-3 ps-3 mb-3">
				<h5 class="text-center">Specifications</h5>
				{#each Object.entries(product.characteristics) as spec}
					<p><b>{spec[0]}:</b> {spec[1]}</p>
				{/each}
			</div>
			<div class="text-center mb-3">
				<Button
					class="btn-success btn-lg"
					on:click={() => {
						modalOpened = true;
					}}>Buy</Button
				>
			</div>

			{#if get(credentials).role === 'ADMIN'}
				<div>
					<a class="btn btn-secondary" href="/admin/update-product/{productId}">Update</a>
				</div>
			{/if}

			<Modal
				autoFocus
				backdrop
				body={false}
				centered={false}
				fullscreen={false}
				isOpen={modalOpened}
				keyboard
				returnFocusAfterClose
				scrollable={false}
				size="md"
				unmountOnClose
			>
				<Form class="p-4">
					<FormGroup>
						<Label>Quantity</Label>
						<Input
							type="number"
							min={1}
							max={product.availableQuantity}
							bind:value={orderQuantity}
						/>
						<div class="pt-2">
							<Button
								class="btn-success"
								on:click={() => {
									privateApi.addProductToOrder(productId, orderQuantity).then((it) => {
										if (is_successful(it.status)) alert('Success fully added');
										else alert('Error occured');
									});
								}}>Add to order</Button
							>
							<Button
								class="btn-secondary"
								on:click={() => {
									modalOpened = false;
								}}>Cancel</Button
							>
						</div>
					</FormGroup>
				</Form>
			</Modal>
		{/await}
		{#await loadCommentsPromise then comments}
			<Container fluid class="border rounded-4 pt-3 ps-3 mb-3">
				<strong>Comments:</strong>
				{#if comments.content.length == 0}
					<p>No comments yet!</p>
				{:else}
					{#each comments.content as comment}
						<div class="border rounded-3 pb-1 pt-2 ps-2">
							{#if comment.ownerId == get(credentials).username}
								<button
									class="btn btn-sm btn-danger"
									on:click={() => {
										privateApi.deleteComment(comment.id).then(() => {
											loadCommentsPromise = loadComments();
										});
									}}>delete</button
								><br />
							{/if}
							Rate: {comment.rate} / 5<br />
							<b>{comment.ownerId}:</b>
							{comment.content}<br />
						</div>
					{/each}
				{/if}
				<div class="form row g-3 mb-3">
					<div class="col-sm-2">
						<label for="rate-input">Set product rate</label>
						<Input type="number" max={5} min={0} bind:value={commentRequest.rate} />
					</div>
					<div class="col">
						<label for="content-input">Input comment:</label>
						<Input type="text" bind:value={commentRequest.content} />
					</div>
					<div class="row pt-3 justify-content-center">
						<button
							class="btn btn-success col-sm-3"
							on:click={() => {
								privateApi.registerComment(commentRequest).then(() => {
									loadCommentsPromise = loadComments();
								});
							}}>Send comment</button
						>
					</div>
				</div>
				<!-- <Pagination>
				 
				</Pagination> -->
			</Container>
		{/await}
	</Container>
</section>
 