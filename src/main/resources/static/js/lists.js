let feedPage = 0;
let feedLoading = false;
let feedHasMore = true;

const feedStream = document.getElementById("feedStream");
const sentinel = document.getElementById("feedSentinel");

async function loadMoreFeed(size = 10) {
  if (feedLoading || !feedHasMore) return;

  feedLoading = true;

  try {
    const profileId = feedStream.dataset.profileId || null;
    const response = await fetch(`/searchLists?page=${feedPage}&size=${size}&id=${profileId}`);

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

    // HTML inserted before sentinel, which has to be last element on feedStream
    sentinel.insertAdjacentHTML("beforebegin", html);
    empty.classList.add("visually-hidden");

    feedPage++;

  } catch (e) {
    console.error(e);
  } finally {
    feedLoading = false;
  }
}

const observer = new IntersectionObserver(
  (entries) => {
    if (entries[0].isIntersecting) {
      loadMoreFeed(10);
    }
  },
  {
    root: feedStream,
    rootMargin: "150px" // starts prior to feed end
  }
);

observer.observe(sentinel);

// first load
loadMoreFeed(10);