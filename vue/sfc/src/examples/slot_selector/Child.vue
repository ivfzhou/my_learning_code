<script setup>

</script>

<template>
  <div>
    <p>Child</p>
    <slot/>
  </div>
</template>

<style scoped>
:slotted(p) {
  color: red;
}
</style>
