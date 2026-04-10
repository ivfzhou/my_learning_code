import {ref} from 'vue'

export default {
    setup() {
        const demoDescription = ref('this is split file usage.')
        return {
            demoDescription
        }
    },
    template: `<span>{{ demoDescription }}</span>`
}
