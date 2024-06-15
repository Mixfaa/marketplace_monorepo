<script lang="ts">
	import { PrivateMarketplaceApi } from '@/api/marketplaceApi';
	import { Container } from '@sveltestrap/sveltestrap';
    import Selector from '@/components/selector/Selector.svelte'
	import SelectorItem from '@/components/selector/SelectorItem.svelte';

	const privateApi = PrivateMarketplaceApi.fromLocalStorage();

	async function fetchProducts(query: string, page: number) {
		const elements = (await privateApi.findProducts(query, page, 15)).data.content as any[];
		return elements;
	}
</script>

<section>
	<Container fluid>
        <Selector fetchElements={fetchProducts} let:item>
            <p slot="header">Select products</p>
           <SelectorItem {item} handler={(element) => {
                privateApi.deleteProduct(element.id)
           }}><p>{item.caption}</p>
           </SelectorItem>
        </Selector>

    </Container>
</section>
