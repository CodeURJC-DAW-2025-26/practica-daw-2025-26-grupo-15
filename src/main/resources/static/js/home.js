var currentPage = 0;

async function searchUsers(size) {
    console.log("hola");

    const input = document.getElementById("sidebarSearch");
    const name = input.value.trim();

    const resultsContainer = document.getElementById("searchResults");

    if (!name) {
        resultsContainer.innerHTML = "<p class='meta'>Type something to search</p>";
        return;
    }

    try {
        const response = await fetch(
            `/searchUsers?name=${encodeURIComponent(name)}&page=${currentPage++}&size=${size}`,
            {
                method: "GET",
            }
        );

        if (!response.ok) {
            throw new Error("Server error");
        }

        const html = await response.text();

        if (currentPage === 0) {
            resultsContainer.innerHTML = html;      
        } else {
            resultsContainer.innerHTML += html; // scroll infinito
        }

    } catch (error) {
        console.log(error);
        resultsContainer.innerHTML = "<p class='meta'>Error loading users</p>";
    }
}