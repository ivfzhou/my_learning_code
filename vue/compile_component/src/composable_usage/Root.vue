<script setup>
import {ref} from 'vue'
import {useMouseTrack} from "@/composable_usage/mouse_track.js";
import {useFetch} from "@/composable_usage/fetch.js";

const {x, y} = useMouseTrack()

const url = ref('')
const {data, error} = useFetch(url)
</script>

<template>
  <div>
    <div>Mouse position: {{ x }}, {{ y }}</div>
  </div>
  <div>
    <input v-model="url" placeholder="Enter a URL"/>
    <div v-if="error">Oops! Error encountered: {{ error.message }}</div>
    <div v-else-if="data">
      Data loaded:
      <pre>{{ data }}</pre>
    </div>
    <div v-else>Loading...</div>
  </div>
</template>

<style scoped>

</style>
