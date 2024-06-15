<script lang="ts">
	import {
		DEFAULT_PAGE_SIZE,
		MarketplaceSort,
		PrivateMarketplaceApi,
		findProducts,
		loadCategories,
		loadProducts
	} from '@/api/marketplaceApi';
	import NavItem from '../components/NavItem.svelte';
	import { Container } from '@sveltestrap/sveltestrap';
	import ProductCard from '@/components/product/ProductCard.svelte';

	const privateApi = PrivateMarketplaceApi.fromLocalStorage();
</script>

<svelte:head>
	<title>Home</title>
	<meta name="description" content="Svelte demo app" />
</svelte:head>

<section>
	<div class="container">
		<div class="sidebar">
			{#await loadCategories(privateApi, 0, DEFAULT_PAGE_SIZE) then categoriesPage}
				<h3>Categories</h3>
				<ul class="nav flex-column">
					{#each categoriesPage.content as category}
						<NavItem data={category} />
					{/each}
				</ul>
			{/await}
		</div>

		<Container fluid class="border rounded-3 shadow-lg p-3">
			<h1 class="text-center">Our most popular products</h1>
			{#await findProducts(privateApi, {}, 'ORDER_COUNT_DESCENDING', 0, DEFAULT_PAGE_SIZE) then productsPage}
				<div class="container gap-4">
					{#each productsPage.content as product}
						<ProductCard {product} />
					{/each}
				</div>
			{/await}
		</Container>
	</div>
</section>
 