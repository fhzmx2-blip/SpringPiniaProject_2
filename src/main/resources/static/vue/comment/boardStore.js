const {defineStore} = Pinia
BoardStore=defineStore('board_comment',{

	state:()=>({
		list:[],
		curpage:1,
		totalpage:0,
		board_no:0,
		sessionId:'',
		count:0,
		msg:'',
		stomp:null, // 알림
		updateMsg:{},
		updateReplyNo:null,
		replyMsg:{},
		reReplyNo:null
	}),

	actions:{
		setCommentData(res)
		{
			console.log(res.data)
			this.list=res.data.list
			this.curpage=res.data.curpage
			this.totalpage=res.data.totalpage
			this.count=res.data.count
		},
		async boardCommentListData(board_no){
			this.board_no=board_no
			const res=await api.get('/reply/list_vue',{
				params:{
					page:this.curpage,
					board_no:board_no
				}
			})
			this.setCommentData(res)
		},
		async boardCommentInsert(msgRef){
			if(this.msg==='')
			{
				msgRef?.focus()
				return
			}
			const res=await api.post('/reply/insert_vue',{
				page:this.curpage,
				board_no:this.board_no,
				msg:this.msg
			})
			this.setCommentData(res)
			
		}
	}
})