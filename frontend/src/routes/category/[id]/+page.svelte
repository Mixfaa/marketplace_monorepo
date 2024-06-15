<script lang="ts">
	import { page } from '$app/stores';
	import {
		DEFAULT_PAGE_SIZE,
		PrivateMarketplaceApi,
		findCategory,
		findProducts,
		type ICategoryDto,
		type IPage,
		type IProductDto
	} from '@/api/marketplaceApi';
	import SortsSelector from '@/components/SortsSelector.svelte';
	import ProductCard from '@/components/product/ProductCard.svelte';
	import {
		Button,
		Container,
		Form,
		FormGroup,
		Input,
		InputGroup,
		Label,
		ListGroup,
		ListGroupItem,
		Modal
	} from '@sveltestrap/sveltestrap';
	const id: string = $page.params.id;

	const privateApi = PrivateMarketplaceApi.fromLocalStorage();

	const filter = {
		criterias: [{}]
	};

	let selectedSort = 'NONE';
	let filtersModal = false;
	let productsPromise = findProducts(privateApi, filter, selectedSort, 0, DEFAULT_PAGE_SIZE);

	let prop: string;
	let filterType: string;
	let value: string = '';
	let values: string[] = [];

	async function loadData() {
		const category = await findCategory(privateApi, id);
		prop = category.requiredProps[0];

		filter.criterias = [];
		filter.criterias.push({
			field: 'allRelatedCategoriesIds',
			type: 'In',
			values: [`${id}`]
		});

		return category;
	}
</script>

<section>
	<Button on:click={() => (filtersModal = true)}>Apply filters</Button>
	{#await loadData() then category}
		{#await productsPromise then productsPage}
			<Container class="gap-4 border rounded-3 shadow-lg p-3">
				{#each productsPage.content as product}
					<ProductCard {product} />
				{/each}
			</Container>
		{/await}
		<Modal
			autoFocus
			backdrop
			body={false}
			centered={false}
			fullscreen={false}
			isOpen={filtersModal}
			keyboard
			returnFocusAfterClose
			scrollable={true}
			size="xl"
			unmountOnClose
		>
			<Form class="p-3">
				<FormGroup>
					<SortsSelector
						bind:selectedSort
						onChange={(sort) => {
							productsPromise = findProducts(privateApi, filter, sort);
						}}
					/>
				</FormGroup>
				<FormGroup class="border g-3 p-3 rounded-3">
					<Label>Add filter</Label>
					<Input type="select" bind:value={prop}>
						{#each category.requiredProps as property}
							<option>{property}</option>
						{/each}
					</Input>
					<Label>Select filter type</Label>
					<Input type="select" bind:value={filterType}>
						{#each ['In', 'Is', 'All', 'Nin'] as type}
							<option>{type}</option>
						{/each}
					</Input>
					{#if filterType !== 'Is'}
						<FormGroup class="gap-3">
							<ListGroup class="pt-1 mb-1">
								{#each values as propValue}
									<ListGroupItem>
										<InputGroup>
											<Input type="text" bind:value={propValue} class="form-control" h />
											<Button
												class="btn-sm btn-danger pb-2"
												on:click={() => {
													values.splice(values.indexOf(propValue), 1);
													values = values;
												}}>remove</Button
											>
										</InputGroup>
									</ListGroupItem>
								{/each}
							</ListGroup>
							<Button
								class="btn-success"
								on:click={() => {
									values.push('');
									values = values;
								}}>Add</Button
							>
						</FormGroup>
					{:else}
						<FormGroup class="gap-3">
							<Label>Property value</Label>
							{#await privateApi.listIndexesForWrapped(id, prop) then indexes}
								<Input type="select" bind:value>
									{#each indexes as option}
										<option>{option}</option>
									{/each}
								</Input>
							{/await}
						</FormGroup>
					{/if}
					<hr />
					<Button
						class="btn-success"
						on:click={() => {
							if (filterType === 'Is') {
								const newCriteria = {
									type: filterType,
									field: `characteristics.${prop}`,
									value: value
								};
								filter.criterias.push(newCriteria);
							} else {
								const newCriteria = {
									type: filterType,
									field: `characteristics.${prop}`,
									values: values
								};

								filter.criterias.push(newCriteria);
							}

							productsPromise = findProducts(privateApi, filter, selectedSort);
						}}>Add filter</Button
					>
				</FormGroup>
				<FormGroup>
					<Button
						class="btn-danger"
						on:click={() => {
							filter.criterias = [];
							productsPromise = findProducts(privateApi, filter, selectedSort);
						}}>Clear filters</Button
					>
					<Button on:click={() => (filtersModal = false)}>Cancel</Button>
				</FormGroup>
			</Form>
		</Modal>
	{/await}
</section>
