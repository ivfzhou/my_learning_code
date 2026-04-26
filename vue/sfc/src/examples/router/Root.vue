<script setup>
import {onMounted, ref} from 'vue'
import MyRouterLink from "@/examples/router/MyRouterLink.vue";

const id = ref(1)
const name = ref('zs')
const id2 = ref('abc')
const nestedPath = ref('')
const anyPath = ref('')
onMounted(() => {
  const style = getComputedStyle(document.getElementById('myRouterLink'))
  console.log(style.scrollMarginTop)
})
</script>

<template>
  <div>
    id: {{ id }}, name: {{ name }}<br/>
    set id: <input @change.self="id = parseInt($event.target.value)"/><br/>
    set name: <input @change.self="name = $event.target.value"/><br/>
    <RouterLink :to="`/dynamicParams/${id}/${name}`">Dynamic Params Route</RouterLink>
  </div>
  <hr/>

  <div id="id2">
    id2: {{ id2 }}<br/>
    set id2: <input @change.self="id2 = $event.target.value"/><br/>
    <RouterLink :to="`/regular/${id2}`">Regular Route</RouterLink>
  </div>
  <hr/>

  <div>
    nestedPath: {{ nestedPath }}<br/>
    set userType: <input @change.self="nestedPath = $event.target.value"/><br/>
    <RouterLink :to="`/nested/${nestedPath}`">Nested Route</RouterLink>
    <br/>
    <RouterLink :to="`/nested2/${nestedPath}`">Nested Route 2</RouterLink>
  </div>
  <hr/>

  <div>
    <RouterLink replace to="/regular/123">Replace Route</RouterLink>
  </div>
  <hr/>

  <div>
    <RouterLink :to="{name: 'root', params:{param:'123'}}">Naming Route</RouterLink>
    <br/>
    <RouterLink to="/namingView2">Naming Route 2</RouterLink>
    <br/>
    <RouterLink to="/namingView3/nested">Naming Route 3</RouterLink>
  </div>
  <hr/>

  <div>
    <RouterLink to="/redirect">Redirect Route</RouterLink>
  </div>
  <hr/>

  <div>
    <RouterLink to="/alias/1">Alias Route</RouterLink>
    <br/>
    <RouterLink to="/alias/2">Alias Route 2</RouterLink>
    <br/>
    <RouterLink to="/alias/3">Alias Route 3</RouterLink>
  </div>
  <hr/>

  <div>
    <RouterLink to="/props/abc">Prop Route</RouterLink>
    <br/>
    <RouterLink to="/props1/hjk">Prop Route 1</RouterLink>
    <br/>
    <RouterLink to="/props2">Prop Route 2</RouterLink>
    <br/>
    <RouterLink to="/props3?key=value">Prop Route 3</RouterLink>
    <br/>
    <RouterLink to="/props4">Prop Route 4</RouterLink>
  </div>
  <hr/>

  <div>
    <RouterLink activeClass="router-link-active" exactActiveClass="router-link-exact-active" to="/hit">
      Hit Route
    </RouterLink>
    <br/>
    <RouterLink to="/hit/exact">Hit Exact Route</RouterLink>
  </div>

  <div>
    set anyPath: <input @change.self="anyPath = $event.target.value"/><br/>
    <RouterLink :to="anyPath">Any Route</RouterLink>
  </div>
  <hr/>

  <div id="myRouterLink">
    <MyRouterLink :to="`/dynamicParams/${id}/${name}`">MyRouterLink</MyRouterLink>
  </div>
  <hr/>

  <div>
    <RouterView v-slot="{Component, route}">
      <component :is="Component" :b="1" :c="true" a="b" :fullPath="route.fullPath"/>
    </RouterView>
    <RouterView name="left"/>
    <RouterView name="right"/>
  </div>
</template>

<style scoped>
.router-link-active {
  color: green;
}

.router-link-exact-active {
  font-size: 2em;
}
</style>
