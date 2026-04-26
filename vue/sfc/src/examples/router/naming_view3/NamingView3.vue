<script setup>

</script>

<template>
  <div class="root">
    <p>NamingView3</p>
    <p>fullPath: {{ $route.fullPath }}</p>
    <RouterView/>
    <RouterView name="left"/>
    <RouterView name="right"/>
  </div>
</template>

<style scoped>
.root {
  border: 2px solid black;
  margin: 2px;
}
</style>
