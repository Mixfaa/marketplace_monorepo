<script lang="ts">
	import { Pagination, PaginationItem, PaginationLink } from '@sveltestrap/sveltestrap';

	export let onFirst: () => void;
	export let onNext: (page: number) => boolean;

	let currentPage = 0;
</script>

<section>
	<Pagination>
		<PaginationItem>
			<PaginationLink
				first
				on:click={() => {
					if (currentPage != 0) {
						currentPage = 0;
						onFirst();
					}
				}}>1</PaginationLink
			>
		</PaginationItem>
		<PaginationItem>
			<PaginationLink
				next
				on:click={() => {
					if (onNext(currentPage + 1)) ++currentPage;
				}}
			/>
		</PaginationItem>
	</Pagination>
</section>
 