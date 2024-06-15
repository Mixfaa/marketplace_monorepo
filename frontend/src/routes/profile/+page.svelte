<script lang="ts">
	import {
		DEFAULT_PAGE_SIZE,
		PrivateMarketplaceApi,
		is_successful,
		loadMyComments,
		loadOrderBuilder,
		loadProduct,
		loadUser,
		type IOrderBuilder,
		type IOrderDto,
		type IProductDto
	} from '@/api/marketplaceApi';
	import SimplePagination from '@/components/SimplePagination.svelte';
	import {
		Button, 
		Container,
		Dropdown,
		DropdownItem,
		DropdownMenu,
		DropdownToggle,
		Form,
		FormGroup,
		Input,
		Label,
		ListGroup,
		ListGroupItem,
		Modal, 
	} from '@sveltestrap/sveltestrap';

	const privateApi = PrivateMarketplaceApi.fromLocalStorage();

	async function loadMyOrders() {
		return (await privateApi.listMyOrders(0, 15)).data.content as IOrderDto[];
	}

	let modalOpened = false;


	let shippingAddress: string = '';
	let promocode: string = '';
	let newShippingAddress: string = '';

	let userPromise = loadUser(privateApi)

	let orderPromise = loadOrderBuilder(privateApi);
	let commentsPromise = loadMyComments(privateApi, 0, DEFAULT_PAGE_SIZE);

	let myOrdersPromise = loadMyOrders();
</script>

<section>
	{#await userPromise  then user}
		<Container fluid class="order-container">
			<h1 class="text-center">Hello {user.firstname}!</h1>

			{#await orderPromise then orderBuilder}
				{#if Object.keys(orderBuilder.products).length != 0}
					<p class="section-title">Your current order:</p>
					<Container fluid class="border rounded-5 shadow-lg pt-3 mb-3 pb-3">
						<ListGroup>
							{#each Object.entries(orderBuilder.products) as entry}
								{#await loadProduct(privateApi, entry[0]) then product}
									<ListGroupItem>
										{product.caption} : {entry[1]}
										<Button
											class="btn-danger btn-sm"
											on:click={() => {
												privateApi.removeProductFromOrder(product.id).then(() => {
													orderPromise = loadOrderBuilder(privateApi);
												});
											}}>Remove</Button
										>
									</ListGroupItem>
								{/await}
							{/each}
						</ListGroup>
					</Container>
					<div class="flex text-center">
						<Button
							class="btn-lg btn-success"
							on:click={() => {
								modalOpened = true;
							}}>Make order!</Button
						>
					</div>
				{:else}
					<p class="text-center">You have no products in your order.</p>
				{/if}
			{/await}
		</Container>

		<Container fluid style="max-height=50vh border rounded p-3">
			<h1 class="text-center">Previous orders</h1>
			{#await myOrdersPromise then orders}
				{#each orders as order}
					<div class="border p-1 rounded">
						<p><strong>Order id:</strong> {order.id}</p>
						<p><strong>Status:</strong> {order.status}</p>
						<p><strong>Shipping address:</strong> {order.shippingAddress}</p>
						<Dropdown>
							<DropdownToggle color="primary" caret>Products</DropdownToggle>
							<DropdownMenu>
								{#each order.products as realizedProduct}
									<DropdownItem
										>{realizedProduct.caption} ({realizedProduct.quantity}) : ${realizedProduct.price}</DropdownItem
									>
								{/each}
							</DropdownMenu>
						</Dropdown>
					</div>
				{/each}
			{/await}
		</Container>

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
					{#if user.shippingAddresses.length == 0}
						<Label>Add shipping address</Label>
						<Input type="text" bind:value={newShippingAddress} />
						<Button
							class="mt-2"
							on:click={() => {
								privateApi.addShippingAddress(newShippingAddress).then(() => {
									userPromise = loadUser(privateApi)
								});
							}}>Add</Button
						>
					{:else}
						<Label>Shipping address</Label>
						<Input type="select" bind:value={shippingAddress}>
							{#each user.shippingAddresses as option}
								<option>{option}</option>
							{/each}
						</Input>
					{/if}
				</FormGroup>
				<FormGroup>
					<Label>Promocode</Label>
					<Input type="text" bind:value={promocode} />
				</FormGroup>
				<FormGroup>
					<Button
						class="btn-success"
						on:click={() => {
							privateApi.makeOrder(shippingAddress, promocode).then((result) => {
								if (is_successful(result.status)) {
									alert('Order successfully made');
									myOrdersPromise = loadMyOrders();
									orderPromise = loadOrderBuilder(privateApi);
								} else alert('Error occured');
							});
						}}>Make order</Button
					>
					<Button
						class="btn-secondary"
						on:click={() => {
							modalOpened = false;
						}}>Cancel</Button
					>
				</FormGroup>
			</Form>
		</Modal>
	{/await}
	{#await commentsPromise then commentsPage}
		{#if commentsPage.content.length != 0}
			<div class="mt-3">
				<h1 class="text-center">Your comments</h1>
				<Container fluid style="max-height=50vh border rounded">
					<ListGroup>
						{#each commentsPage.content as comment}
							{#await loadProduct(privateApi,comment.productId) then product}
								<ListGroupItem
									>{product.caption}: {comment.content} ({comment.rate}/5)
									<Button
										class="btn-danger btn-sm"
										on:click={() => {
											privateApi.deleteComment(comment.id).then(() => {
												commentsPromise = loadMyComments(privateApi, 0, DEFAULT_PAGE_SIZE);
											});
										}}>Remove</Button
									>
								</ListGroupItem>
							{/await}
						{/each}
					</ListGroup>
					<div class="d-flex text-center justify-content-center">
						<SimplePagination
							onNext={(page) => {
								if (commentsPage.totalPages > page) {
									commentsPromise = loadMyComments(privateApi, page, DEFAULT_PAGE_SIZE);
									return true;
								}
								return false;
							}}
							onFirst={() => {
								commentsPromise = loadMyComments(privateApi, 0, DEFAULT_PAGE_SIZE);
							}}
						/>
					</div>
				</Container>
			</div>
		{/if}
	{/await}
</section>
