import type UserBasicInfoDTO from "~/dtos/UserBasicInfoDTO";
import type { UserDTO } from "~/dtos/UserDTO";
import type { PageInfoUserDTO } from "~/dtos/PageInfoUserDTO";
import type UserEditDTO from "~/dtos/UserEditDTO";
import type FollowingSuggestionDTO from "~/dtos/FollowingSuggestionDTO";


const API_URL = "/api/v1/users";


export async function getUser(id: number) {
  const res = await fetch(`${API_URL}/${id}`);
  if (!res.ok) {
    throw new Error("User not found");
  }
  return await res.json();
}

export async function getUsersByName(name: string, user: UserDTO | null, page: number): Promise<{data: UserDTO[], hasMore: boolean}> {
  const res = await fetch(`${API_URL}/?page=${page}&size=5&excludedId=${user ? user.id : ''}&nameFilter=${encodeURIComponent(name)}`);
  if (!res.ok) {
    throw new Error("Failed to fetch users");
  }
  const data = await res.json();
  const itemsArray: UserDTO[] = Array.isArray(data) ? data : (data.content || data.data || []);
  

  return {
      hasMore: data.page.number < data.page.totalPages,
      data: itemsArray
  };
}

export async function registerUser(
  name: string,
  email: string,
  password: string
): Promise<UserDTO> {

  const response = await fetch(`${API_URL}/`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ email, encodedPassword: password, name })
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || "Failed to register user. Please try again.");
  }
  return await response.json();
}

export async function getFollowRequests(): Promise<UserBasicInfoDTO[]> {
  const res = await fetch(`${API_URL}/me/follow-requests/`);
  if (!res.ok) {
    throw new Error("Failed to fetch follow requests");
  }
  return await res.json();
}

export async function updateProfile(user: UserEditDTO, userId: number) {
  const res = await fetch(`${API_URL}/${userId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(user)
  });

  if (!res.ok){
    throw new Error("Failed to update profile");
  }
  return await res.json();
}

export async function updateProfilePhoto(photoFile: File, userId: number): Promise<void> {
  const formData = new FormData();
    formData.append("imageFile", photoFile);
  
  const res = await fetch(`${API_URL}/${userId}/images`, {
    method: "POST",
    body: formData
  });

  if (!res.ok){
    throw new Error("Failed to update profile photo");
  }
  return await res.json();
}

export async function sendFollowRequest(targetId:string) {
  const res = await fetch(`${API_URL}/${targetId}/follow-requests/`,
    {
      method: "POST",
      headers: {
      "Content-Type": "application/json"
    },
    });

  if(!res.ok){
    throw new Error("Failed to send the follow requests");
  }
  return await res.json();
}

  export async function acceptFollowRequest(targetId:string){
    const res = await fetch(`${API_URL}/me/follow-requests/${targetId}`,
      {
        method: "POST",
        headers: {"Content-Type" : "application/json" }
      });

      if(!res.ok){
        throw new Error("Failed to accept the follow request")
      }

  }
  export async function declineFollowRequest(targetId:string) {
    const res = await fetch(`${API_URL}/me/follow-requests/${targetId}`,
      {
        method:"DELETE",
        headers:{"Content-Type" : "application/json"}
      });

      if(!res.ok){
        throw new Error("Failed to decline the follow request")
      }
  }
  export async function unFollowUser(targetId:string) {
    const res = await fetch(`${API_URL}/me/follows/${targetId}`,
      {
        method:"DELETE",
        headers:{"Content-Type" : "application/json"}
      });
      if(!res.ok){
        throw new Error("Failed to decline the follow request")
      }
  }

export async function checkByName(
  name: string,
  page = 0,
  size = 20,
  signal?: AbortSignal,
): Promise<PageInfoUserDTO> {
  const res = await fetch(
    `${API_URL}/?nameFilter=${encodeURIComponent(name)}&page=${page}&size=${size}`,
    { signal },
  );
  if (!res.ok) {
    throw new Error("Failed to check the name");
  }
  return await res.json();
}

export async function isUsernameAvailableBySearch(
  name: string,
  signal?: AbortSignal,
): Promise<boolean> {
  const normalizedName = name.trim().toLowerCase();
  if (!normalizedName) {
    return false;
  }

  let pageNumber = 0;
  const pageSize = 20;

  while (true) {
    const page = await checkByName(normalizedName, pageNumber, pageSize, signal);

    const exactMatch = page.content.some(
      (user) => user.name.trim().toLowerCase() === normalizedName,
    );

    if (exactMatch) {
      return false;
    }

    if (page.page.number + 1 >= page.page.totalPages) {
      return true;
    }

    pageNumber += 1;
      }
  }

  export async function removeFollower(toRemoveId:string){
      const res = await fetch(`${API_URL}/me/followers/${toRemoveId}`,
        {
          method:"DELETE",
          headers:{"Content-Type" : "application/json"}
        });
        if(!res.ok){
          throw new Error("Failed to remove follower")
    }
  }

    export async function deleteProfile(targetId:string){
      const res = await fetch(`${API_URL}/${targetId}`,
        {
          method:"DELETE",
          headers:{"Content-Type" : "application/json"}
        });
        if(!res.ok){
          throw new Error("Failed to delete profile")
    }
}

export async function getFollowingSuggestions(): Promise<FollowingSuggestionDTO[]> {
  const res = await fetch(`${API_URL}/me/following-suggestions/`);
  if(!res.ok){
    throw new Error("Failed to fetch following suggestions")
  }
  return await res.json();
}
  


