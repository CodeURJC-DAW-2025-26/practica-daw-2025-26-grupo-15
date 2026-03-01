let feedPage = 0;
let feedLoading = false;
let feedHasMore = true;
let currentPetition;

let observer;
const feedStream = document.getElementById("feedStream");
const sentinel = document.getElementById("feedSentinel");
const spinner = document.getElementById("loadingSpinner");

const defaultPageSize = 10;

const operations = new Map([
  ["l", ({ page, size, profileId }) => `/searchLists?page=${page}&size=${size}&userId=${encodeURIComponent(profileId ?? "")}`],
  ["p", ({ page, size }) => `/searchPosts?page=${page}&size=${size}`],
]);

async function loadMoreFeed(size = defaultPageSize) {
  currentPetition = feedStream?.dataset?.petition ?? "";  
  const profileId = feedStream?.dataset?.profileId ?? null;

  if (currentPetition === null || !operations.has(currentPetition)) return;  
  if (profileId === null && currentPetition === "l") return;
  if (feedLoading || !feedHasMore) return;
    

  feedLoading = true;

  if (feedPage > 0){
    spinner.classList.toggle("visually-hidden");
    //timeout so that the spinner is seen
    await new Promise(r => setTimeout(r, 3000));
  }

  try {
    const buildUrl = operations.get(currentPetition);
    const query = buildUrl({ page: feedPage, size, profileId });
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

    spinner.classList.add("visually-hidden")

    // HTML inserted before sentinel, which has to be last element on feedStream
    sentinel.insertAdjacentHTML("beforebegin", html);
    if (empty) empty.classList.add("visually-hidden");

    feedPage++;

  } catch (e) {
    console.error(e);
  } finally {
    feedLoading = false;
  }
}


if (feedStream && sentinel) {
    observer = new IntersectionObserver(
    (entries) => {
        if (entries[0].isIntersecting) {
        loadMoreFeed(defaultPageSize);
        }
    },
    {
        root: feedStream,
        rootMargin: "150px" // starts prior to feed end
    }
    );

    observer.observe(sentinel);
    loadMoreFeed(defaultPageSize);
}

