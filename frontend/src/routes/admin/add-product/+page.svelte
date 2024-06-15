<script lang="ts">
	import ImagesUpload from '@/components/ImagesUpload.svelte';
	import {
		DEFAULT_PAGE_SIZE,
		PrivateMarketplaceApi,
		is_successful,
		type ICategoryDto,
		type IProductRegisterDto
	} from '@/api/marketplaceApi';
	import DefaultCard from '@/components/DefaultCard.svelte';
	import { Input } from '@sveltestrap/sveltestrap';

	const privateApi = PrivateMarketplaceApi.fromLocalStorage();

	let categoryName: string = '';
	let newProp: string = '';
	let newImage: string = '';
	let fetchedCategories: ICategoryDto[] = [];

	function addRequiredProps(category: ICategoryDto) {
		category.requiredProps.forEach((prop) => {
			if (!registerRequest.characteristics.has(prop))
				registerRequest.characteristics.set(prop, 'unset');
			registerRequest = registerRequest;
		});
	}

	async function fetchCategories(name: string, page: number) {
		const result = await privateApi.findCategories(name, page, DEFAULT_PAGE_SIZE);
		fetchedCategories = result.data.content as ICategoryDto[];
	}

	let registerRequest: IProductRegisterDto = {
		caption: '',
		categories: [],
		description: '',
		price: 0,
		images: [],
		characteristics: new Map(),
		availableQuantity: 0
	};

	async function registerProduct() {
		const result = await privateApi.registerProduct(registerRequest);
		if (is_successful(result.status)) alert('Product successfully registered!');
		console.log(result.data);
	}
</script>

<section>
	<DefaultCard>
		<h2>Добавить Товар</h2>
		<div class="mb-3">
			<label for="productName" class="form-label">Product Name</label>
			<input
				type="text"
				class="form-control"
				id="productName"
				bind:value={registerRequest.caption}
				required
			/>
		</div>
		<div class="mb-3">
			<label for="productPrice" class="form-label">Product Price</label>
			<input
				type="number"
				class="form-control"
				id="productPrice"
				bind:value={registerRequest.price}
				required
			/>
		</div>
		<div class="mb-3">
			<label for="productDescription" class="form-label">Description</label>
			<textarea
				bind:value={registerRequest.description}
				class="form-control"
				id="productDescription"
				rows="3"
				required
			></textarea>
		</div>
		<div class="mb-3">
			<label for="quantity-field">Available quantity</label>
			<Input
				id="quantity-field"
				type="number"
				min={0}
				placeholder="Avaiable quantity"
				bind:value={registerRequest.availableQuantity}
			/>
		</div>
		<div class="container-lg border border-5 p-3 rounded-4 shadow mb-3">
			<p>Select categories</p>
			<div class="row">
				<label for="search_field">Category name</label>
				<div class="col">
					<input class="form-control" type="text" bind:value={categoryName} id="search_field" />
				</div>
				<div class="col">
					<button
						class="btn btn-primary"
						on:click={() => {
							fetchCategories(categoryName, 0);
						}}>Search</button
					>
				</div>
			</div>

			<div class="list-group">
				{#each fetchedCategories as category}
					{#if registerRequest.categories.includes(category.id)}
						<button
							class="btn btn-danger list-group-item"
							on:click={() => {
								const index = registerRequest.categories.indexOf(category.id);
								registerRequest.categories.splice(index);
								registerRequest = registerRequest;
							}}>Remove {category.name}</button
						>
					{:else}
						<button
							class="btn btn-success list-group-item"
							on:click={() => {
								registerRequest.categories.push(category.id);
								registerRequest = registerRequest;

								addRequiredProps(category);
							}}>Add {category.name}</button
						>
					{/if}
				{/each}
			</div>
		</div>
		{#if registerRequest.characteristics.size != 0}
			<div class="container-lg border border-5 p-3 rounded-4 shadow mb-3">
				<p>Edit product specs</p>

				<div class="list-group">
					{#each registerRequest.characteristics as prop}
						<div class="list-group-item">
							<p>{prop[0]}</p>
							<input
								type="text"
								on:input={(event) => {
									registerRequest.characteristics.set(prop[0], event.currentTarget.value);
									registerRequest = registerRequest;
								}}
							/>
							<button
								on:click={() => {
									registerRequest.characteristics.delete(prop[0]);
									registerRequest = registerRequest;
								}}>X</button
							>
						</div>
					{/each}
				</div>
			</div>
		{/if}
		<div class="container-lg border border-5 p-3 rounded-4 shadow mb-3">
			<p>Add prop</p>
			<div class="row">
				<div class="col">
					<input class="form-control" type="text" bind:value={newProp} />
				</div>
				<div class="col">
					<button
						class="btn btn-primary"
						on:click={() => {
							registerRequest.characteristics.set(newProp, '');
							registerRequest = registerRequest;
						}}>Add</button
					>
				</div>
			</div>
		</div>

		<div class="container-lg border border-5 p-3 rounded-4 shadow mb-3">
			<p>Add image</p>
			<div class="row">
				<div class="col">
					<input class="form-control" type="text" bind:value={newImage} />
				</div>
				<div class="col">
					<button
						class="btn btn-primary"
						on:click={() => {
							if (!registerRequest.images.includes(newImage)) {
								registerRequest.images.push(newImage);
								registerRequest = registerRequest;
							}
						}}>Add</button
					>
				</div>
				<ImagesUpload
					onUpload={(url) => {
						registerRequest.images.push(url);
						registerRequest = registerRequest;
					}}
				/>
			</div>
			<div class="list-group">
				{#each registerRequest.images as image}
					<div class="list-group-item">
						<div class="row">
							<div class="col">
								<p>{image}</p>
							</div>
							<div class="col">
								<button
									on:click={() => {
										const index = registerRequest.images.indexOf(image);
										registerRequest.images.splice(index);
										registerRequest = registerRequest;
									}}>X</button
								>
							</div>
						</div>
					</div>
				{/each}
			</div>
		</div>
		<button
			on:click={() => {
				console.log(registerRequest);
				registerProduct();
			}}
			class="btn btn-primary">Submit</button
		>
	</DefaultCard>
</section>
