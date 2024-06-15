<script lang="ts">
	import type { IProductDto } from '@/api/marketplaceApi';

	export let product: IProductDto;
	export let onGoto: () => void = () => {}
</script>

<section>
	<div class="card" style="width: 18rem;">
		<img class="card-img-top" src={product.images[0]} />
		<div class="card-body">
			<a href="/product/{product.id}" class="card-title" on:click={onGoto}>{product.caption}</a>
			<p>{product.description}</p>
			<strong>${product.actualPrice}</strong>
		</div>
	</div>
</section>
