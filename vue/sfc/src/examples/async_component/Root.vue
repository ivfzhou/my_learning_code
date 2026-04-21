<script setup>
import {defineAsyncComponent} from 'vue'

// const AsyncComponent = defineAsyncComponent(() => new Promise(resolve => setTimeout(() => resolve(import('@/async_component/AsyncComponent.vue')), 5000)))
// const AsyncComponent = defineAsyncComponent(() => import('@/async_component/AsyncComponent.vue'))

// const AsyncComponent = defineAsyncComponent({
//   // 加载函数
//   // loader: () => import('@/async_component/AsyncComponent.vue'),
//   loader: () => new Promise(resolve => setTimeout(() => resolve(import('@/async_component/AsyncComponent.vue')), 4000)),
//
//   // 加载异步组件时使用的组件
//   loadingComponent: LoadingComponent,
//   // 展示加载组件前的延迟时间，默认为 200ms
//   delay: 200,
//
//   // 加载失败后展示的组件
//   errorComponent: ErrorComponent,
//   // 如果提供了一个 timeout 时间限制，并超时了
//   // 也会显示这里配置的报错组件，默认值是：Infinity
//   timeout: 3000,
// })

// const AsyncComponent = defineAsyncComponent({
//   loader: () => new Promise(resolve => setTimeout(() => resolve(import('@/async_component/AsyncComponent.vue')), 4000)),
//   hydrate: hydrateOnIdle(/* 传递可选的最大超时 */1000)
// })

// const show = ref(false)
// setTimeout(() => {
//   show.value = true
// }, 5000)
// const AsyncComponent = defineAsyncComponent({
//   loader: () => import('@/async_component/AsyncComponent.vue'),
//   hydrate: hydrateOnVisible()
// })

// const AsyncComponent = defineAsyncComponent({
//   loader: () => import('@/async_component/AsyncComponent.vue'),
//   hydrate: hydrateOnMediaQuery('(min-width: 5000px)')
// })

// const AsyncComponent = defineAsyncComponent({
//   loader: () => import('@/async_component/AsyncComponent.vue'),
//   hydrate: hydrateOnInteraction('click')
// })

const myStrategy = (hydrate, forEachElement) => {
  console.log('myStrategy')
  // forEachElement 是一个遍历组件未激活的 DOM 中所有根元素的辅助函数，
  // 因为根元素可能是一个片段而非单个元素
  forEachElement(el => {
    console.log('el', el)
  })
  // 准备好时调用 `hydrate`
  hydrate()
  return () => {
    // 如必要，返回一个销毁函数
  }
}
const AsyncComponent = defineAsyncComponent({
  loader: () => import('@/examples/async_component/AsyncComponent.vue'),
  hydrate: myStrategy
})
</script>

<template>
  <!-- <AsyncComponent v-if="show"/> -->
  <div style="border: 2px solid black">
    <AsyncComponent/>
  </div>
</template>

<style scoped>

</style>
