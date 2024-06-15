<script lang="ts">
	import {
		DEFAULT_PAGE_SIZE,
		PrivateMarketplaceApi,
		is_successful,
		type ICategoryDto,
		type ICategoryRegisterDto,
		type IProductRegisterDto
	} from '@/api/marketplaceApi';
	import DefaultCard from '@/components/DefaultCard.svelte'; 
	import Selector from '@/components/selector/Selector.svelte';
	import ElementsList from '@/components/list/ElementsList.svelte';
	import ListButton from '@/components/list/ListButton.svelte';
	import SelectorItem from '@/components/selector/SelectorItem.svelte';

	const privateApi = PrivateMarketplaceApi.fromLocalStorage();

	let registerRequest: ICategoryRegisterDto = {
		name: '',
		parentCategory: undefined,
		requiredProps: []
	};

	let parentCategoryName: string

	async function fetchCategories(query: string, page: number) {
		const result = await privateApi.findCategories(query, page, DEFAULT_PAGE_SIZE);
		return result.data.content as any[];
	}

	async function registerCategory() {
		const response = await privateApi.registerCategory(registerRequest);
		if (is_successful(response.status)) alert('Category successfully registered');
	}

	let newPropName: string = '';
</script>

<section>
	<DefaultCard>
		<h2>Добавить Категорию</h2>
		<div class="mb-3">
			<label for="categoryName" class="form-label">Category Name</label>
			<input
				type="text"
				class="form-control"
				id="categoryName"
				required
				bind:value={registerRequest.name}
			/>
		</div>
		<div>
			<Selector fetchElements={fetchCategories} let:item>
				<p slot="header">Select parent category</p>
				<SelectorItem
					handler={() => {
						registerRequest.parentCategory = item.id;
						parentCategoryName = item.name
					}}
					{item}
				>
					<p>{item.name}</p>
				</SelectorItem>
				<div slot="additional" class="pt-3">
					{#if registerRequest.parentCategory?.length != 0}
						<p>Selected category: {parentCategoryName}</p>
					{/if}
				</div>
			</Selector>
		</div>
		<div class="mb-3">
			<label for="propName" class="form-label">New property Name</label>
			<input type="text" class="form-control" id="propName" required bind:value={newPropName} />
			<button
				class="btn btn-info mt-2"
				on:click={() => {
					registerRequest.requiredProps.push(newPropName);
					registerRequest = registerRequest;
					newPropName = '';
				}}>Add</button
			>
			<ElementsList elements={registerRequest.requiredProps} let:item>
				<ListButton
					{item}
					btnText="remove"
					handler={() => {
						registerRequest.requiredProps.splice(registerRequest.requiredProps.indexOf(item), 1);
						registerRequest = registerRequest;
					}}
				/>
			</ElementsList>
		</div>
		<div class="d-flex justify-content-center align-items-center">
			<button class="btn btn-success" on:click={registerCategory}>Register</button>
		</div>
	</DefaultCard>
</section>
