<script setup>
import {mapActions, mapState, mapWritableState, storeToRefs} from 'pinia'
import {useCounterStore} from '@/stores/counter.js'

const counterStore = useCounterStore()
// const {count} = counterStore
const {count} = storeToRefs(counterStore)

const ms1 = mapState(counterStore, ['count'])
const ms2 = mapState(counterStore, {
  count1: 'count',
  count2: store => store.count,
  count3(store) {
    return store.count + this.count2()
  }
})
console.log('mapState1=', ms1, ', mapState2=', ms2)

const ms3 = mapWritableState(counterStore, ['count'])
const ms4 = mapWritableState(counterStore, {
  count1: 'count'
})
console.log('mapWritableState3=', ms3, ', mapWritableState4=', ms4)

const ma1 = mapActions(counterStore, ['increment'])
const ma2 = mapActions(counterStore, {increment: 'increment'})
console.log('mapActions1=', ma1, ', mapActions2=', ma2)

counterStore.$subscribe(
    (mutation, state) => {
      console.log('mutation=', mutation, ', state=', state)
    },
    {detached: false, flush: 'pre'}
)

counterStore.$onAction(
    ({name, store, args, after, onError}) => {
      console.log('name=', name, ', store=', store, ', args=', args, ', after=', after, ', onError=', onError)
      const startTime = Date.now()
      after(result => {
        console.log('result=', result, ', finished time', Date.now() - startTime)
      })
      onError(err => {
        console.log('err=', err)
      })
    },
    false
)
</script>

<template>
  <div>
    count: {{ counterStore.count }}<br/>
    count: {{ count }}<br/>
    thirdCount: {{ counterStore.thirdCount }}<br/>
    <button @click.self="counterStore.increment">Increment</button>
    <button @click.self="counterStore.increment2">Increment2</button>
    <button @click.self="console.log($pinia)">$pinia</button>
  </div>
</template>

<style scoped>

</style>
