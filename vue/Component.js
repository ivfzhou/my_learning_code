import {ref} from 'vue'

export default {
    props: ['title'],
    async setup(props, ctx) {
        ctx.emits('fn')
        ctx.attrs
    },
    template: `#app`,
    directives: {
        // 在模板中启用 v-highlight
        highlight: {
            /* ... */
        }
    }
}
