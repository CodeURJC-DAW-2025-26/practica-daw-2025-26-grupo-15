let currentPage = 0;
let prevName = "";
let loading = false;
let hasMore = true;

const resultsListContainer = document.getElementById("searchResults");
const resultsMainContainer = document.getElementById("searchContainer");
const input = document.getElementById("sidebarSearch");

// Load first page (call it on input with debounce)
async function searchFirstPage(size = 25) {
  const name = input.value.trim();

  if (!name) {
    resultsMainContainer.classList.add("visually-hidden");
    resultsListContainer.innerHTML = "";
    prevName = "";
    currentPage = 0;
    hasMore = false;
    return;
  }

  resultsMainContainer.classList.remove("visually-hidden");

  prevName = name;
  currentPage = 0;
  hasMore = true;
  resultsListContainer.scrollTop = 0;

  await loadPage(name, currentPage, size, true);
}

async function loadMore(size = 25) {
  const name = input.value.trim();
  if (!name) return;

  if (name !== prevName) {
    // If the query changed, start from page 0
    await searchFirstPage(size);
    return;
  }

  if (loading || !hasMore) return;

  await loadPage(name, currentPage, size, false);
}

async function loadPage(name, page, size, reset) {
  if (loading) return;
  loading = true;

  try {
    const response = await fetch(
      `/searchUsers?name=${encodeURIComponent(name)}&page=${page}&size=${size}`,
      {method: "GET"}
    );

    if (!response.ok) throw new Error("Server error");

    // headers
    hasMore = response.headers.get("X-Has-More") === "true";
    const count = parseInt(response.headers.get("X-Results-Count") || "0", 10);

    const html = await response.text();

    if (reset) resultsListContainer.innerHTML = html;
    else if (count > 0) resultsListContainer.insertAdjacentHTML("beforeend", html);
    // If count == 0 and page > 0, the fragment adds nothing (template behavior)

    // Move to the next page only when the request succeeds
    currentPage = page + 1;

  } catch (e) {
    console.error(e);
  } finally {
    loading = false;
  }
}

// Scroll inside the container
resultsListContainer.addEventListener("scroll", () => {
  const nearBottom =
    resultsListContainer.scrollTop + resultsListContainer.clientHeight >=
    resultsListContainer.scrollHeight - 50;

  if (nearBottom) loadMore(25);
});

// Search while typing (debounce)
let t;

input.addEventListener("input", () => {
  clearTimeout(t);
  t = setTimeout(() => {
    searchFirstPage(25);
  }, 2000); // 2 seconds
});
