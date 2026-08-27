const { createApp, onMounted, ref } = Vue
const { createPinia } = Pinia
const commentApp = createApp({
    setup() {

        const store = useBoardStore();
        const msgRef = ref(null)

        onMounted(() => {
            store.sessionId = SESSION_ID
            store.boardCommentListData(BOARDNO)
        })

        return {
            store,
            msgRef
        }
    }
})
commentApp.use(createPinia())
commentApp.mount("#comment")