let currentPage = 0;
let prevName = "";
let loading = false;
let hasMore = true;

const resultsListContainer = document.getElementById("searchResults");
const resultsMainContainer = document.getElementById("searchContainer");
const input = document.getElementById("sidebarSearch");

// Carga primera página (llámalo al escribir con debounce)
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
    // si cambió la query, empieza de 0
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
    // si count == 0 y page>0, el fragment no añade nada (por template) y no ensuciamos

    // avanzamos página SOLO si la request fue OK
    currentPage = page + 1;

  } catch (e) {
    console.error(e);
  } finally {
    loading = false;
  }
}

// Scroll dentro del contenedor
resultsListContainer.addEventListener("scroll", () => {
  const nearBottom =
    resultsListContainer.scrollTop + resultsListContainer.clientHeight >=
    resultsListContainer.scrollHeight - 50;

  if (nearBottom) loadMore(25);
});

// Buscar mientras escribes (debounce)
let t;

input.addEventListener("input", () => {
  clearTimeout(t);
  t = setTimeout(() => {
    searchFirstPage(25);
  }, 2000); // 2 segundos
});