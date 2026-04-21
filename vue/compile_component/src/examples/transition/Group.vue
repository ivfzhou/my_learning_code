<script setup>
import {ref} from 'vue'

const list = ref([1, 2, 3, 4, 5])

function deleteItem() {
  const index = Math.random() * list.value.length
  list.value.splice(index, 1)
}

function addItem() {
  const index = Math.random() * list.value.length
  const nextValue = list.value.length ? Math.max(...list.value) + 1 : 1
  list.value.splice(index, 0, nextValue)
}

function reverseList() {
  list.value.reverse()
}

function sortList() {
  list.value.sort((a, b) => a - b)
}

function shuffleList() {
  list.value.sort(() => Math.random() - 0.5)
}
</script>

<template>
  <div>
    <button @click="addItem">添加</button>
    <button @click="deleteItem">删除</button>
    <button @click="reverseList">反转</button>
    <button @click="sortList">排序</button>
    <button @click="shuffleList">打乱</button>
  </div>
  <div>
    <TransitionGroup tag="ul" mode="out-in">
      <li v-for="item in list" :key="item">{{ item }}</li>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.v-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.v-enter-to {
  opacity: 1;
}

.v-enter-active {
  transition: all 0.5s ease;
}

.v-leave-from {
  opacity: 1;
}

.v-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.v-leave-active {
  transition: all 0.5s ease;
}

.v-move {
  transition: all 0.5s ease;
}

/* 确保将离开的元素从布局流中删除，以便能够正确地计算移动的动画。 */
.v-leave-active {
  position: absolute;
}
</style>
