<script lang="ts">
	import DefaultCard from '../DefaultCard.svelte';

	export let elements: any[];
</script>

<section>
	{#if elements.length != 0}
		<div class="container-lg border border-5 p-3 rounded-4 shadow mb-3">
			<div class="pt-3">
				<div class="list-group">
					{#each elements as item}
						<slot {item}><p>{item}</p></slot>
					{/each}
				</div>
			</div>
		</div>
	{/if}
</section>
