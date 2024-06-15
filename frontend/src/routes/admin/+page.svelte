<script lang="ts">
</script>

<section>
	<a href="/admin/add-category">Add category</a>
	<a href="/admin/add-product">Add product</a>
	<a href="/admin/delete-product">Delete product</a>
	<a href="/admin/add-discount">Add discount</a>
	<a href="/admin/delete-discount">Delete discount</a>
</section>

<style>
	section {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-top: 50px;
	}

	a {
		text-decoration: none;
		color: white;
		background-color: #808080;
		padding: 15px 30px;
		margin: 10px;
		border-radius: 5px;
		font-size: 18px;
		transition:
			background-color 0.3s,
			transform 0.3s;
	}

	a:hover {
		background-color: #a9a9a9;
		transform: scale(1.05);
	}

	a:active {
		background-color: #696969;
	}
</style>
