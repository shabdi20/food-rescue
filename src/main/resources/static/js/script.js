document.addEventListener('click',e=>{if(e.target.closest('.menu-btn'))document.querySelector('.side').classList.toggle('open');
 const c=e.target.closest('[data-confirm]');if(c&&!confirm(c.dataset.confirm))e.preventDefault();});
