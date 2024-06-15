<script lang="ts">
	import { Button, Form, FormGroup, Input, Label, Modal } from '@sveltestrap/sveltestrap';
	import { PrivateMarketplaceApi, isAuthenticated, logout } from '../api/marketplaceApi';
	import SearchModal from './SearchModal.svelte';
	let searchModal = false;

	const privateApi = PrivateMarketplaceApi.fromLocalStorage();
</script>

<header>
	<div class="logo">
		<div class="iconLogo">
			<img
				width="60px"
				height="60px"
				src="/rep/12.png"
				alt="Market Icon"
				on:click={() => {
					searchModal = true; 
				}}
			/>
		</div>
		<a href="/">Stuck&Stock MARKET</a>
	</div>
	<div class="user">
		{#if isAuthenticated()}
			<a href="/profile"><img src="/rep/usericon.png" alt="User" /></a>
		{:else}
			<a href="/login"><img src="/rep/usericon.png" alt="User" /></a>
		{/if}
		{#if isAuthenticated()}
			<p class="text-success">Authenticated</p>
			<button on:click={logout} class="btn btn-danger">Logout</button>
		{/if}
	</div>

	<SearchModal modalOpened={searchModal} onCancel={() => searchModal = false}/>
	
</header>
