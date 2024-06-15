<script>
    	let clazz = '';
		export {clazz as class}; 
</script>

<section class="{clazz}">
    <div class="d-flex justify-content-center align-items-center">
        <div class="col-7 col-md-5 col-lg-7 col-xl-7">
            <div class="card rounded-5 shadow-lg">
                <div class="card-body p-5 body_colored2 rounded-5">
                    <slot />
                </div>
            </div>
        </div>
    </div>
</section>
