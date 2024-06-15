<script lang="ts">
	import { PrivateMarketplaceApi, findCategory, type ICategoryDto } from '@/api/marketplaceApi';

	const privateApi = PrivateMarketplaceApi.fromLocalStorage();
	export let data: ICategoryDto;
</script>

<li class="nav-item">
	<a class="nav-link" href="/category/{data.id}">{data.name}</a>
	<ul>
		{#each data.subcategoriesIds as subcategoryId}
			{#await findCategory(privateApi, subcategoryId) then category}
				<li><a href="/category/{subcategoryId}">{category.name}</a></li>
			{/await}
		{/each}
	</ul>
</li>
