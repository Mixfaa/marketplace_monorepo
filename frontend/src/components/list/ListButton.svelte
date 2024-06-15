<script lang="ts">
	export let item: any;
    export let btnText: string
    export let btnClass = 'btn btn-info'
	export let handler: ((item: any) => void)
</script>

<section>
	<div class="row">
		<div class="col">
			<slot {item}><p>{item}</p></slot>
		</div>
		<div class="col">
			<button on:click={handler} class="{btnClass}">{btnText}</button>
		</div>
	</div>
</section>
