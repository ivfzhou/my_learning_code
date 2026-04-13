import {createApp, ref} from 'vue'
import ComponentA from './ComponentA.js'
import ComponentB from './ComponentB.js'

createApp({
    setup() {
        const name = ref('ComponentA')

        return {
            name
        }
    },
    components: {ComponentA, ComponentB},
    template: `
<div>
    <component-a></component-a>
    <br/>
    <component-b></component-b>
    <table>
        <thead>
            <tr is="vue:component-b"></tr>
        </thead>
    </table>
</div>
`,
}).mount('#app')
