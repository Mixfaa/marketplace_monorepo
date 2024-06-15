<script>
	import {
		PrivateMarketplaceApi,
		PublicMarketplaceApi,
		authenticate,
		is_successful
	} from '../../api/marketplaceApi';
	const availableRoles = ['ADMIN', 'CUSTOMER'];
	const marketplaceApi = PublicMarketplaceApi.make();

	let emailCodeSent = false;
	let emailToSendCode = '';

	async function sendEmailCode() {
		emailCodeSent = true;
		const response = await marketplaceApi.sendEmailCode(emailToSendCode);
		alert(response.status);
		if (is_successful(response.status)) emailCodeSent = true;
	}

	let registerRequest = {
		username: '',
		firstname: '',
		lastname: '',
		password: '',
		mailCode: '',
		role: 'CUSTOMER',
		adminSecret: 'jMR[T0i=#j02!_;hKOE0Ttyf6'
	};

	async function register() {
		const registerResult = await marketplaceApi.register(registerRequest);
		if (is_successful(registerResult.status)) {
			authenticate(registerRequest.username, registerRequest.password);
			alert(`Logged in as ${registerRequest.username}`);
		}
	}
</script>

<section>
	{#if !emailCodeSent}
		<div class="d-flex justify-content-center align-items-center">
			<div class="col-7 col-md-7 col-lg-7 col-xl-5">
				<div class="card rounded-5 shadow-lg">
					<div class="card-body p-5 body_colored2 rounded-5">
						<h2 class="text-center mb-5">Send Email code</h2>
						<div class="form-outline mb-3">
							<input
								bind:value={emailToSendCode}
								type="email"
								id="email_field"
								class="form-control form-control-lg"
								required
							/>
							<label class="form-label ms-1" for="email_field">Email</label>
						</div>
						<div class="d-flex justify-content-center">
							<button on:click={sendEmailCode} class="btn btn-success btn-block btn-lg text-body"
								>Send code
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	{:else}
		<div class="d-flex justify-content-center align-items-center">
			<div class="col-7 col-md-7 col-lg-7 col-xl-5">
				<div class="card rounded-5 shadow-lg">
					<div class="card-body p-5 body_colored2 rounded-5">
						<h2 class="text-center mb-5">Create an account</h2>
						<div class="form-outline mb-3">
							<input
								bind:value={registerRequest.username}
								type="text"
								id="username_field"
								class="form-control form-control-lg"
								required
							/>
							<label class="form-label ms-1" for="username_field">Username</label>
						</div>
						<div class="form-outline mb-3">
							<input
								bind:value={registerRequest.firstname}
								type="text"
								id="firstname_field"
								class="form-control form-control-lg"
								required
							/>
							<label class="form-label ms-1" for="firstname_field">First name</label>
						</div>
						<div class="form-outline mb-3">
							<input
								bind:value={registerRequest.lastname}
								type="text"
								id="lastname_field"
								class="form-control form-control-lg"
								required
							/>
							<label class="form-label ms-1" for="lastname_field">Last name</label>
						</div>
						<div class="form-outline mb-3">
							<input
								bind:value={registerRequest.mailCode}
								type="number"
								id="emailcode_field"
								class="form-control form-control-lg"
								required
							/>
							<label class="form-label ms-1" for="emailcode_field">Email code</label>
						</div>
						<div class="form-outline mb-3">
							<input
								bind:value={registerRequest.password}
								type="password"
								id="pass_field"
								class="form-control form-control-lg"
								required
							/>
							<label class="form-label ms-1" for="pass_field">Password</label>
						</div>
						<details>
							<summary>Specify role</summary>
							<select bind:value={registerRequest.role}>
								{#each availableRoles as role}
									<option value={role}>
										{role}
									</option>
								{/each}
							</select>

							{#if registerRequest.role == 'ADMIN'}
								<div class="form-outline mb-3">
									<input
										bind:value={registerRequest.adminSecret}
										type="text"
										id="adminSecret_field"
										class="form-control form-control-lg"
										required
									/>
									<label class="form-label ms-1" for="adminSecret_field">Admin Secret</label>
								</div>
							{/if}
						</details>

						<div class="d-flex justify-content-center">
							<button on:click={register} class="btn btn-success btn-block btn-lg text-body"
								>Register</button
							>
						</div>
					</div>
				</div>
			</div>
		</div>
	{/if}
</section>
