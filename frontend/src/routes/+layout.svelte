<script>
	import Header from '../components/Header.svelte';
	import '../styles.css';
	import Footer from '../components/Footer.svelte';
</script>

<div class="app">
	<Header />

	<main>
		<button class="back-button" on:click={() => window.history.back()}>Back</button>
		<slot />
	</main>

	<Footer />
</div>

<style>
	.app {
		display: flex;
		flex-direction: column;
		min-height: 100vh;
	}

	main {
		flex: 1;
		display: flex;
		flex-direction: column;
		padding: 1rem;
		width: 100%;
		max-width: 64rem;
		margin: 0 auto;
		box-sizing: border-box;
	}
 
</style>
