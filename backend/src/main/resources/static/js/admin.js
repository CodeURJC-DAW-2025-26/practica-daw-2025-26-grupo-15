let feedPage = 0;
let feedLoading = false;
let feedHasMore = true;
let currentPetition;
let adminOption = "u";
let filter = "";

let observer;
const feedScroll = document.getElementById("feedScroll");
const feedStream = document.getElementById("feedStream");
const sentinel = document.getElementById("feedSentinel");
const inputFilter = document.getElementById("inputFilter");

const modalsContainer = document.getElementById("modalsContainer");

const defaultPageSize = 10;



function setAdminOption(opt){
  if (!["u","l","e"].includes(opt)) return;
  adminOption = opt;

  reloadFeed()  
}

function setFilter(){
    filter = (inputFilter?.value ?? "").trim();

    reloadFeed();
}

function reloadFeed(){
    //reset feed after changing option
  feedPage = 0;
  feedHasMore = true;
  feedStream.innerHTML = "";
    //conect observer again in case it was disconected
  observer.observe(sentinel);   
    //load first page again after changing
  loadMoreFeed(defaultPageSize);
}

async function loadMoreFeed(size = defaultPageSize) {
  currentPetition = feedStream?.dataset?.petition ?? "";  

  currentPetition += adminOption;

  if (currentPetition === null || (currentPetition !== "al" && currentPetition !== "au" && currentPetition !== "ae")) return;  

  if (feedLoading || !feedHasMore) return;
    

  feedLoading = true;

  try {
    const query = `/adminSearch?page=${feedPage}&size=${size}&petition=${currentPetition}&inputFilter=${filter}`;
    const response = await fetch(query);

    if (!response.ok) throw new Error(`HTTP ${response.status}`);

    const hasMoreHeader = response.headers.get("X-Has-More");
    if (hasMoreHeader !== null) {
      feedHasMore = hasMoreHeader === "true";
    }

    const html = await response.text();

    const countHeader = response.headers.get("X-Results-Count");
    const count = countHeader ? parseInt(countHeader, 10) : 0;

    const empty = document.getElementById('feedEmpty');

    // Stop if no results
    if (count === 0) {
      feedHasMore = false;
      observer.disconnect();
      if (feedPage === 0) {
        if (empty) empty.classList.remove('visually-hidden');
      }
      return;
    }

    feedStream.innerHTML += html;
    await loadModals(currentPetition, feedPage, size);
    setTableHeader();
    if (empty) empty.classList.add("visually-hidden");

    feedPage++;

  } catch (e) {
    console.error(e);
  } finally {
    feedLoading = false;
  }
}

async function loadModals(petition){
    const rows = document.querySelectorAll("[data-entity-id]");
    const ids = [...rows].map(r => r.dataset.entityId);

    const params = new URLSearchParams();
    params.set("petition", petition);
    ids.forEach(id => params.append("ids", id));

    const modalsHtml = await fetch("/loadModals?" + params.toString()).then(r => r.text());
    document.querySelector("#modalsContainer").innerHTML = modalsHtml;
}

function setTableHeader(){
    const header = document.getElementById("tableHeader");
    if (currentPetition === "au"){
        header.innerHTML = `<tr>
                                <th>User</th>
                                <th>Email</th>
                                <th>Roles</th>
                                <th class="text-center">Followers</th>
                                <th class="text-center">Following</th>
                                <th></th>
                            </tr>`;
    } else if (currentPetition === "al") {
        header.innerHTML = `<tr>
                                <th>Title</th>
                                <th>Topic</th>
                                <th class="text-center">Owner</th>
                                <th></th>
                            </tr>`;
    } else if (currentPetition === "ae"){
        header.innerHTML = `<tr>
                                <th>Title</th>
                                <th>List</th>
                                <th class="text-center">Owner</th>
                                <th></th>
                            </tr>`;
    } else {
        throw new Error("Invalid option");
    }
}


if (feedScroll && feedStream && sentinel) {
    observer = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) {
        loadMoreFeed(defaultPageSize);
    }
    }, {
    root: feedScroll,        
    rootMargin: "150px"
    });

    observer.observe(sentinel);
    loadMoreFeed(defaultPageSize);
}

