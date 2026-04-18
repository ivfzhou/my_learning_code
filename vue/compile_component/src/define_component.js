import {version, defineComponent, ref, h, defineAsyncComponent} from 'vue'

console.log(version)

const comp = defineComponent({
    setup() {
        const count = ref(0)
        return () => h('div', count.value)
    },
})

const comp2 = defineComponent(
    (props, ctx) => {
        const count = ref(2)
        return () => h('div', count.value)
    },
)

export default {comp, comp2}
