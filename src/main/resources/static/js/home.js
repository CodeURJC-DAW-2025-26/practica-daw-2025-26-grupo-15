var currentPage = 0;
var prevName = "";
const resultsListContainer = document.getElementById("searchResults");

async function searchUsers(size) {
    console.log("hola");

    const input = document.getElementById("sidebarSearch");
    const name = input.value.trim();

    const resultsMainContainer = document.getElementById("searchContainer");

    if (!name) { 
        resultsMainContainer.classList.add("visually-hidden");
        return;
    }

    resultsMainContainer.classList.remove("visually-hidden");

    try {
        const response = await fetch(
            `/searchUsers?name=${encodeURIComponent(name)}&page=${currentPage}&size=${size}`,
            {
                method: "GET",
            }
        );

        if (!response.ok) {
            throw new Error("Server error");
        }

        const html = await response.text();
        if (!html || html.trim().length === 0){
            currentPage = 0;
            prevName = "";
            resultsListContainer.innerHTML = `<span class="sidebar-search-results__title">No users found for the name ${name}</span>` ; 
        } else if (currentPage === 0 || name !== prevName) {
            currentPage = 0;
            prevName = name;
            resultsListContainer.innerHTML = html;      
        } else {
            currentPage++;
            resultsListContainer.innerHTML += html; // scroll infinito
        }

    } catch (error) {
        console.log(error);
        resultsListContainer.innerHTML = "<p class='meta'>Error loading users</p>";
    }

    resultsListContainer.addEventListener("scroll", () => {

    const nearBottom =
        resultsListContainer.scrollTop + resultsContainer.clientHeight >=
        resultsListContainer.scrollHeight - 50;

    if (nearBottom) {
        searchUsers(25);
    }
    });
}