<script setup>
import {ref} from 'vue'

const id = ref(1)
const name = ref('zs')
const orderId = ref('abc')
const userType = ref('')
</script>

<template>
  <div>
    id: {{ id }}<br/>
    name: {{ name }}<br/>
    set id: <input @change.self="id = parseInt($event.target.value)"/><br/>
    set name: <input @change.self="name = $event.target.value"/><br/>
    <RouterLink :to="`/user/${id}/${name}`">Route To User</RouterLink>
  </div>
  <hr/>

  <div>
    orderId: {{ orderId }}<br/>
    set orderId: <input @change.self="orderId = $event.target.value"/><br/>
    <RouterLink :to="`/order/${orderId}`">Route To Order</RouterLink>
  </div>
  <hr/>

  <div>
    userType: {{ userType }}<br/>
    set userType: <input @change.self="userType = $event.target.value"/><br/>
    <RouterLink :to="`/user2/${userType}`">Route To User2</RouterLink>
    <br/>
    <RouterLink :to="`/user3/${userType}`">Route To User3</RouterLink>
  </div>
  <hr/>

  <div>
    <RouterLink :to="{name: 'Profile'}">Route To Profile</RouterLink>
  </div>
  <hr/>

  <div>
    <RouterView/>
  </div>
</template>

<style scoped>

</style>
