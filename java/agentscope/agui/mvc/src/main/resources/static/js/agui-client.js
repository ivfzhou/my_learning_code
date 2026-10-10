/**
 * AG-UI Protocol Client
 *
 * A minimal JavaScript client for communicating with AG-UI protocol servers.
 *
 * @example
 * const client = new AguiClient('/agui/run');
 * await client.run({
 *     threadId: 'thread-123',
 *     runId: 'run-456',
 *     messages: [{ id: 'msg-1', role: 'user', content: 'Hello!' }]
 * }, {
 *     onTextContent: (delta) => console.log(delta),
 *     onReasoningContent: (delta) => console.log('Reasoning:', delta),
 *     onRunFinished: () => console.log('Done')
 * });
 */
export class AguiClient {
    /**
     * Create a new AG-UI client.
     * @param {string} endpoint - The AG-UI run endpoint URL
     */
    constructor(endpoint) {
        this.endpoint = endpoint
        this.abortController = null
    }

    /**
     * Abort the current run if one is in progress.
     * This will close the SSE connection and trigger agent interruption on the backend.
     */
    abort() {
        if (this.abortController) {
            console.log('Aborting current run...')
            this.abortController.abort()
            this.abortController = null
        }
    }

    /**
     * Check if a run is currently in progress.
     * @returns {boolean} True if running
     */
    isRunning() {
        return this.abortController !== null
    }

    /**
     * Run an agent with the given input.
     * @param {Object} input - The run input
     * @param {string} input.threadId - Thread identifier
     * @param {string} input.runId - Run identifier
     * @param {Array} input.messages - Array of messages
     * @param {Array} [input.tools] - Optional tools
     * @param {Array} [input.context] - Optional context
     * @param {Object} [input.state] - Optional state
     * @param {Object} [input.forwardedProps] - Optional forwarded properties
     * @param {Array} [input.resume] - Optional AG-UI interrupt resume entries
     * @param {Object} callbacks - Event callbacks
     * @param {Function} [callbacks.onReasoningMessageStart] - Called when reasoning message starts
     * @param {Function} [callbacks.onReasoningContent] - Called with reasoning content delta
     * @param {Function} [callbacks.onReasoningMessageEnd] - Called when reasoning message ends
     * @returns {Promise} Resolves when the run completes
     */
    async run(input, callbacks = {}) {
        this.abortController = new AbortController()
        const signal = this.abortController.signal

        const response = await fetch(this.endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'text/event-stream'
            },
            body: JSON.stringify(input),
            signal: signal
        })

        if (!response.ok) {
            this.abortController = null
            throw new Error(`HTTP error: ${response.status} ${response.statusText}`)
        }

        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''

        let eventSequence = 0

        try {
            while (true) {
                const {done, value} = await reader.read()

                if (done) break

                const chunk = decoder.decode(value, {stream: true})
                buffer += chunk

                let delimiter = '\n\n'
                let delimiterIndex = buffer.indexOf(delimiter)

                if (delimiterIndex === -1) {
                    delimiter = '\r\n\r\n'
                    delimiterIndex = buffer.indexOf(delimiter)
                }

                while (delimiterIndex !== -1) {
                    const message = buffer.substring(0, delimiterIndex)
                    buffer = buffer.substring(delimiterIndex + delimiter.length)


                    const lines = message.split(/\r?\n/)
                    for (const line of lines) {
                        if (line.startsWith('data:')) {
                            try {
                                const jsonStr = line.startsWith('data: ') ? line.substring(6) : line.substring(5)
                                const event = JSON.parse(jsonStr)
                                eventSequence++
                                this.handleEvent(event, callbacks)
                            } catch (e) {
                                console.warn('Failed to parse event:', line, e)
                            }
                        }
                    }

                    delimiterIndex = buffer.indexOf('\n\n')
                    if (delimiterIndex === -1) {
                        delimiterIndex = buffer.indexOf('\r\n\r\n')
                        if (delimiterIndex !== -1) delimiter = '\r\n\r\n'
                    } else {
                        delimiter = '\n\n'
                    }
                }
            }

            if (buffer.trim()) {
                const lines = buffer.split(/\r?\n/)
                for (const line of lines) {
                    if (line.startsWith('data:')) {
                        try {
                            const jsonStr = line.startsWith('data: ') ? line.substring(6) : line.substring(5)
                            const event = JSON.parse(jsonStr)
                            this.handleEvent(event, callbacks)
                        } catch (e) {
                            console.warn('Failed to parse remaining event:', line, e)
                        }
                    }
                }
            }
        } finally {
            reader.releaseLock()
            this.abortController = null
        }
    }

    /**
     * Handle an AG-UI event.
     * @param {Object} event - The event object
     * @param {Object} callbacks - Event callbacks
     */
    handleEvent(event, callbacks) {
        if (!event || !event.type) {
            console.warn('Invalid event received:', event)
            return
        }

        const type = event.type

        try {
            switch (type) {
                case 'RUN_STARTED':
                    callbacks.onRunStarted?.(event.threadId, event.runId)
                    break

                case 'RUN_FINISHED':
                    callbacks.onRunFinished?.(event.threadId, event.runId, event)
                    break

                case 'RUN_ERROR':
                    callbacks.onError?.(event.message || event.code || 'Run error', event)
                    break

                case 'TEXT_MESSAGE_START':
                    callbacks.onTextMessageStart?.(event.messageId, event.role)
                    break

                case 'TEXT_MESSAGE_CONTENT':
                    const delta = event.delta || ''
                    if (delta) {
                        callbacks.onTextContent?.(delta, event.messageId)
                    }
                    break

                case 'TEXT_MESSAGE_END':
                    callbacks.onTextMessageEnd?.(event.messageId)
                    break

                case 'REASONING_MESSAGE_START':
                    callbacks.onReasoningMessageStart?.(event.messageId, event.role)
                    break

                case 'REASONING_MESSAGE_CONTENT':
                    const reasoningDelta = event.delta || ''
                    if (reasoningDelta) {
                        callbacks.onReasoningContent?.(reasoningDelta, event.messageId)
                    }
                    break

                case 'REASONING_MESSAGE_END':
                    callbacks.onReasoningMessageEnd?.(event.messageId)
                    break

                case 'TOOL_CALL_START':
                    callbacks.onToolCallStart?.(event.toolCallId, event.toolCallName)
                    break

                case 'TOOL_CALL_ARGS':
                    callbacks.onToolCallArgs?.(event.toolCallId, event.delta)
                    break

                case 'TOOL_CALL_END':
                    callbacks.onToolCallEnd?.(event.toolCallId)
                    break

                case 'TOOL_CALL_RESULT':
                    callbacks.onToolCallResult?.(event.toolCallId, event.content, event.messageId)
                    break

                case 'STATE_SNAPSHOT':
                    callbacks.onStateSnapshot?.(event.snapshot)
                    break

                case 'STATE_DELTA':
                    callbacks.onStateDelta?.(event.delta)
                    break

                case 'RAW':
                    if (event.rawEvent?.error) {
                        callbacks.onError?.(event.rawEvent.error)
                    } else {
                        callbacks.onRawEvent?.(event.rawEvent)
                    }
                    break

                case 'CUSTOM':
                    callbacks.onCustomEvent?.(event)
                    break

                default:
                    console.log('Unknown event type:', type, event)
            }
        } catch (error) {
            console.error('Error handling event:', type, error)
        }
    }
}
