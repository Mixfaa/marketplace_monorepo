<script lang="ts">
	export let handler: (element: any, add: boolean) => void;
	export let conatains: (element: any) => boolean;
	export let item;

	let clazz = '';
	export { clazz as class };
</script>

<section class={clazz}>
	<div class="row">
		<div class="col">
			<slot {item} />
		</div>
		<div class="col">
			{#if conatains(item)}
				<button
					class="btn btn-danger"
					on:click={() => {
						handler(item, false);
					}}>Remove</button
				>
			{:else}
				<button
					class="btn btn-success"
					on:click={() => {
						handler(item, true);
					}}>Add</button
				>
			{/if}
		</div>
	</div>
</section>
