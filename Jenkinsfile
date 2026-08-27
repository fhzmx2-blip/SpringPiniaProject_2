pipeline{
	stages{
		stage('Git Connection Check'){
			steps{
				echo "=============="
				echo "Git 연결 확인"
				echo "=============="
				git branch: 'master',
					url: 'https://github.com/fhzmx2-blip/SpringPiniaProject_2.git'
				echo "=============="
				echo "Git 연결 완료"
				echo "=============="
			}
		}
	}
}