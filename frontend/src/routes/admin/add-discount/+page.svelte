<script lang="ts">
	import { PrivateMarketplaceApi, is_successful } from '@/api/marketplaceApi';
	import DefaultCard from '@/components/DefaultCard.svelte';
	import Selector from '@/components/selector/Selector.svelte';
	import AddRemoveItem from '@/components/selector/AddRemoveItem.svelte';

	let privateApi = PrivateMarketplaceApi.fromLocalStorage();

	let promoCodeRequest = {
		code: '',
		description: '',
		discount: 1
	};

	interface ByProductRequest {
		description: string;
		discount: number;
		targetProductsIds: string[];
	}

	let byProductRequest: ByProductRequest = {
		description: '',
		discount: 1,
		targetProductsIds: []
	};

	interface ByCategoryRequest {
		description: string;
		discount: number;
		targetCategoriesIds: string[];
	}

	let byCategoryRequest: ByCategoryRequest = {
		description: '',
		discount: 1,
		targetCategoriesIds: []
	};

	let discountType = 'promocode';

	async function fetchProducts(query: string, page: number) {
		const elements = (await privateApi.findProducts(query, page, 15)).data.content as any[];

		return elements;
	}

	async function fetchCategories(query: string, page: number) {
		const elements = (await privateApi.findCategories(query, page, 15)).data.content as any[];

		return elements;
	}

	async function registerDiscount(request: any) {
		const response = await privateApi.registerDiscount(request);
		if (is_successful(response.status)) alert('Discount registered successfully');
		else alert(`Status: ${response.status}`);
	}
</script>

<section>
	<DefaultCard class="mb-3">
		<select class="form-select" aria-label="Select discount type" bind:value={discountType}>
			<option selected value="promocode">Promocode</option>
			<option value="by-product">By product</option>
			<option value="by-category">By category</option>
		</select>
	</DefaultCard>
	{#if discountType == 'promocode'}
		<DefaultCard class="form">
			<h5 class="text-center">Create Promocode</h5>
			<div class="mb-3">
				<label for="promocode-field" class="form-label">Code:</label>
				<input
					id="promocode-field"
					class="form-control"
					type="text"
					bind:value={promoCodeRequest.code}
				/>
			</div>
			<div class="mb-3">
				<label for="description-field" class="form-label">Description:</label>
				<input
					id="description-field"
					class="form-control"
					type="text"
					bind:value={promoCodeRequest.description}
				/>
			</div>

			<div class="mb-3">
				<label for="discount" class="form-label">Discount {promoCodeRequest.discount}%</label>
				<input
					id="discount"
					type="range"
					class="form-range"
					min="1"
					max="99"
					bind:value={promoCodeRequest.discount}
				/>
			</div>

			<button
				class="btn btn-success"
				on:click={() => {
					registerDiscount(promoCodeRequest);
				}}>Register</button
			>
		</DefaultCard>
	{:else if discountType == 'by-product'}
		<DefaultCard class="form">
			<h5 class="text-center">Create Discount by product</h5>

			<div class="mb-3">
				<label for="description-field" class="form-label">Description:</label>
				<input
					id="description-field"
					class="form-control"
					type="text"
					bind:value={byProductRequest.description}
				/>
			</div>

			<div class="mb-3">
				<label for="discount-field" class="form-label">Discount {byProductRequest.discount}%</label>
				<input
					id="discount-field"
					type="range"
					class="form-range"
					min="1"
					max="99"
					bind:value={byProductRequest.discount}
				/>
			</div>

			<div class="mb-3">
				<Selector fetchElements={fetchProducts} let:item>
					<p slot="header">Select products</p>
					<AddRemoveItem
						{item}
						conatains={(item) => {
							return byProductRequest.targetProductsIds.includes(item.id);
						}}
						handler={(item, add) => {
							if (add) byProductRequest.targetProductsIds.push(item.id);
							else {
								const index = byProductRequest.targetProductsIds.indexOf(item.id);
								byProductRequest.targetProductsIds.splice(index, 1);
							}

							byProductRequest = byProductRequest;
						}}><p>{item.caption}</p></AddRemoveItem
					>
				</Selector>
			</div>
			<button
				class="btn btn-success"
				on:click={() => {
					registerDiscount(byProductRequest);
				}}>Register</button
			>
		</DefaultCard>
	{:else}
		<DefaultCard class="form">
			<h5 class="text-center">Create Discount by category</h5>

			<div class="mb-3">
				<label for="description-field" class="form-label">Description:</label>
				<input
					id="description-field"
					class="form-control"
					type="text"
					bind:value={byCategoryRequest.description}
				/>
			</div>

			<div class="mb-3">
				<label for="discount-field" class="form-label">Discount {byCategoryRequest.discount}%</label
				>
				<input
					id="discount-field"
					type="range"
					class="form-range"
					min="1"
					max="99"
					bind:value={byCategoryRequest.discount}
				/>
			</div>

			<div class="mb-3">
				<Selector fetchElements={fetchCategories} let:item>
					<p slot="header">Select categories</p>
					<AddRemoveItem
						{item}
						conatains={(item) => {
							return byCategoryRequest.targetCategoriesIds.includes(item.id);
						}}
						handler={(item, add) => {
							if (add) byCategoryRequest.targetCategoriesIds.push(item.id);
							else {
								const index = byCategoryRequest.targetCategoriesIds.indexOf(item.id);
								byCategoryRequest.targetCategoriesIds.splice(index, 1);
							}

							byCategoryRequest = byCategoryRequest;
						}}><p>{item.name}</p></AddRemoveItem
					>
				</Selector>
			</div>
			<button
				class="btn btn-success"
				on:click={() => {
					registerDiscount(byCategoryRequest);
				}}>Register</button
			>
		</DefaultCard>
	{/if}
</section>
