

function callLogout(){

    axios({method:'get',url:'/logout'}).finally(()=>{setTimeout(() => {
        window.location.href='/';
    }, 500); });
}