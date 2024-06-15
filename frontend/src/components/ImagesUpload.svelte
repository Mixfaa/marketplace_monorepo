<script lang="ts">
	import {
		MARKET_PLACE_ENDPOINT,
		PrivateMarketplaceApi,
		type IStoredFileDto
	} from '@/api/marketplaceApi';
	import DefaultCard from './DefaultCard.svelte';

	let privateApi = PrivateMarketplaceApi.fromLocalStorage();

	export let onUpload: (url: string) => void;

	let formElement: HTMLFormElement;

	async function upload() {
		const formData = new FormData(formElement);
		const response = (await privateApi.uploadFile(formData)).data as IStoredFileDto;

		const url = `${MARKET_PLACE_ENDPOINT}/file-storage/file/${response.id}/bytes`;
		onUpload(url);
	}
</script>

<section>
	<DefaultCard>
		<div class="form">
			<form bind:this={formElement} id="upload-form">
				<label class="mb-3" for="file">Select image</label>
				<input class="form-control mb-3" type="file" name="file" id="file" />
				<button class="form-control btn btn-success" on:click={upload}>Upload</button>
			</form>
		</div>
	</DefaultCard>
</section>
