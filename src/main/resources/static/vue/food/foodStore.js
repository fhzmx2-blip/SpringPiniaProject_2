const {defineStore} = Pinia
// store : 저장공간 = (처리함수 , 데이터)
// => 전역으로 사용 = 모든 HTML에서 사용이 가능
// => 데이터가 변경 => 자동으로 HTML 갱신 
// => ----------------------------- state / data()
/*
     1. state : HTML에 적용하는 변수 
	 2. actions : 사용자 요청 처리 =>: springBoot와 연동 
	 3. getters : computed 
	    | range / 1,000
		
     Pinia store 
	 ------------------
	  state 
	    |- list:[]
		|- curpage ...
		
	  getters ========== 없는 경우도 있다 
	    |- range
	 
	  actions 
	    |- foodListData
		|- move
	 ------------------
	        |
		   this (Store전체)
     range:(state) => getters에서는 state부분만 받아서 사용 
	   | => this사용도 가능 
	  
*/
const initialState=()=>({
	list:[],
	curpage:1,
	totalpage:0,
	startPage:0,
	endPage:0
})
// store생성 => 새로운 store를 생성 => defindStore
const useFoodStore=defineStore('food_store',{
	state:initialState,
	// store안에 저장된 state를 가지고 새로운 값을 계산해서 반환하는 역할
	// 총액 계산 / 페이지번호 .. / 1,000 => computed:{}
	getters:{
		range:(state)=>{
			const arr=[]
			for(let i=state.startPage;i<=state.endPage;i++)
			{
				arr.push(i) // 맨뒤에 값을 저장 
				// pop() // 뒤에서부터 제거 
			}
			return arr
		}
	},
	// 기능 => 사용자 요청 => 서버연동 
	// methods: { dataRecv()}
	actions:{
		async foodListData(){
			const res=await api.get('/food/list_vue',{
				params:{
					page: this.curpage
				}
			})
			
			console.log(res.data)// Map에 있는 데이터값 받기 
			this.list=res.data.list
			this.curpage=res.data.curpage
			this.totalpage=res.data.totalpage
			this.startPage=res.data.startPage
			this.endPage=res.data.endPage
		},
		move(page){
			this.curpage=page
			this.foodListData()
		}
	}
})