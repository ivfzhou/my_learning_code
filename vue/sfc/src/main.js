import {computed, createApp} from 'vue'
import {createPinia} from 'pinia'

import App from './App.vue'
import router from './router'

const app = createApp(App)

const pinia = createPinia()
pinia.use(({options, store}) => {
    console.log('in use, store.count=', store.count, ', options.hello=', options.hello)

    store.$subscribe((mutation, state) => {
        console.log('in use, mutation=', mutation, ', state=', state)
    })

    store.$onAction(({name, store, args, after, onError}) => {
        console.log('in use, name=', name, ', store=', store, ', args=', args, ', after=', after, ', onError=', onError)
        const startTime = Date.now()
        after(result => {
            console.log('in use, result=', result, ', finished time', Date.now() - startTime)
        })
        onError(err => {
            console.log('in use, err=', err)
        })
    })

    return {
        thirdCount: computed(() => store.count * 3),
        increment2() {
            store.count++
        }
    }
})

app.use(pinia)
app.use(router)

app.mount('#app')
