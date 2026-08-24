const {defineStore} = Pinia
const {nextTick} = Vue
// nextTick => DOM업데이트 => 다음 문장을 실행하고 싶은 경우 
/*
   
*/
const useChatStore=defineStore('chat',{
	// 변수 저장 공간 
	state:()=>({
	   stomp:null, 
	   users:[], // 접속자 목록 
	   messages:[], // 화면에 출력할 채팅 메세지
	   publicMessages:[], // 전체 채팅 메세지 
	   privateMessages:[], // 1:1 채팅 메세지 
	   currentRoom:'public', // 현재 채팅방 
	   loginUser:'', // 로그인 사용자 
	   chatBodyEl:null,// 채팅창 DOM
	   msg:'' // 입력 메세지 
	   
	}),
	// 이벤트 => 사용자 요청한 경우 
	// => kim , hong 
	// => kim_hong , kim+hong hong+kim
	actions:{
		// 채팅방 ID 생성 
		makeRoomId(user1,user2){
			return [
				user1,
				user2
			]
			.sort()
			.join('_')
		},
		// 채팅방 변경 
		changeRoom(user){
			// 전체 채팅 
			if(user==='public')
			{
				this.currentRoom='public'
				this.messages=this.publicMessages
			}
			// 1:1 
			else
			{
				const roomId=this.makeRoomId(this.loginUser,user)
				// 해당방이 없는 경우 
				if(!this.privateMessages[roomId])
				{
				   this.privateMessages[roomId]=[]		
				}	
				this.messages=this.privateMessages[roomId]
			}
			this.scrollToBottom()
		},
		async scrollToBottom(){
			await nextTick()
			// 먼저 마지막에 스크롤바를 내린다 
			if(this.chatBodyEl){
				this.chatBodyEl.scrollTop=
				  this.chatBodyEl.scrollHeight
			}
		},
		// 서버연결 (WebSocket 연동)
		connect(){
			const socket=new SockJS('/chat-ws')
			// stomp 연결 
			this.stomp=Stomp.over(socket)
			// => stomp에 대한 메모리 할당 
			this.stomp.connect({},()=>{
				console.log("WebSocket 연결 성공")
				// 접속자 목록 가지고 오기 
				this.stomp.subscribe(
					'/topic/users',
					msg=>{
						const users=JSON.parse(msg.body)
						// 본인은 제외 
						this.users=users.filter(u=>u!==this.loginUser)
					}
				)
			})
		}
	}
})