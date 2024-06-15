<script lang="ts">
	import { MarketplaceSorts } from "@/api/marketplaceApi";
	import { Input, Label } from "@sveltestrap/sveltestrap";

	export let selectedSort: string;
	export let onChange: (value: string) => void = () =>  {}
</script>

<section>
	<Label>Sort by</Label>
	<Input type="select" bind:value={selectedSort} on:change={(event) => {
		onChange(selectedSort)
	}}>
		{#each MarketplaceSorts as option}
			<option>{option}</option>
		{/each}
	</Input>
</section>
