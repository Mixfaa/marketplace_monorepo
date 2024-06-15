<script lang="ts">
	import {
		DEFAULT_PAGE_SIZE,
		MarketplaceSort,
		MarketplaceSorts,
		PrivateMarketplaceApi,
		findProducts,
		loadProducts,
		type IPage,
		type IProductDto
	} from '@/api/marketplaceApi';
	import {
		Button,
		Container,
		Form,
		FormGroup,
		Input,
		Label,
		Modal,
		Pagination,
		PaginationItem,
		PaginationLink,
		Spinner,
		Theme
	} from '@sveltestrap/sveltestrap';
	import ProductCard from './product/ProductCard.svelte';
	import SimplePagination from './SimplePagination.svelte';
	import SortsSelector from './SortsSelector.svelte';

	export let modalOpened: boolean;
	export let onCancel: () => void;
	const privateApi = PrivateMarketplaceApi.fromLocalStorage();

	let productsPormise: Promise<IPage<IProductDto>> = loadProducts(
		privateApi,
		'',
		0,
		DEFAULT_PAGE_SIZE
	);
	let query: string = '';
	let selectedSort = 'NONE';

	function makeTextQuery(query: string) {
		return {
			criterias: [
				{
					type: 'Text',
					text: query
				}
			]
		};
	}
</script>

<section>
	<Modal
		autoFocus
		backdrop
		body={false}
		centered={false}
		fullscreen={false}
		isOpen={modalOpened}
		keyboard
		returnFocusAfterClose
		scrollable={true}
		size="xl"
		unmountOnClose
	>
		<Form class="p-2">
			<FormGroup>
				<Label>Search query</Label>
				<Input type="text" bind:value={query} />
				<SortsSelector bind:selectedSort/>
				<div class="mt-2">
					<Button
						class="btn-success"
						on:click={() => {
							productsPormise = findProducts(
								privateApi,
								makeTextQuery(query),
								selectedSort,
								0,
								DEFAULT_PAGE_SIZE
							);
						}}>Search</Button
					>
					<Button class="btn-secondary" on:click={onCancel}>Cancel</Button>
				</div>
			</FormGroup>
		</Form>
		{#await productsPormise then productsPage}
			<Container fluid class="d-flex gap-3">
				{#each productsPage.content as product}
					<ProductCard {product} onGoto={onCancel} />
				{/each}
			</Container>
			<div class="d-flex justify-content-center">
				<SimplePagination
					onFirst={() => {
						productsPormise = loadProducts(privateApi, query, 0, DEFAULT_PAGE_SIZE);
					}}
					onNext={(page) => {
						if (productsPage.totalPages >= page) {
							productsPormise = loadProducts(privateApi, query, page, DEFAULT_PAGE_SIZE);
							return true
						}
						return false
					}}
				/>
			</div>
		{/await}
	</Modal>
</section>
