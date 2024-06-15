<script lang="ts">
	export let handler: (element: any) => void;
	export let item;

	let clazz = '';
	export { clazz as class };

	export let btnText: string = 'Select'
</script>

<section class={clazz}>
	<div class="row">
		<div class="col">
			<slot {item} />
		</div>
		<div class="col">
			<button
				class="btn btn-primary"
				on:click={() => {
					handler(item);
				}}>{btnText}</button
			>
		</div>
	</div>
</section>
