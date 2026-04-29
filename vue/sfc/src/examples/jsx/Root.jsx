import {ref, withModifiers} from 'vue'

export const Node1 = () => <div>hello</div>

export const Node2 = () => {
    const value = ref('abc')
    return <div>
        <p>{value.value}</p>
        <input onChangeOnce={withModifiers((e) => {
            value.value = e.target.value
            console.log(value.value)
        }, ['self'])}/>
    </div>
}

export const Node3 = () => {
    const ok = 'abc'
    return <div>{ok ? <div>yes</div> : <span>no</span>}</div>
}

export const Node4 = () => {
    const items = [
        {id: 1, text: 'zs'}
    ]
    return <ul>
        {items.map(({id, text}) => {
            return <li key={id}>{text}</li>
        })}
    </ul>
}

export const Node5 = () => {
    return <div><Node1/></div>
}

export const Node6 = {
    setup(props, {slots}) {
        return () => <div>
            {slots.default()}
            {slots.header({
                key: 'value'
            })}
        </div>
    }
}

export const Node7 = () => <Node6>{{
    default: () => 'default slot content',
    header: ({key}) => {
        return key
    }
}}</Node6>
