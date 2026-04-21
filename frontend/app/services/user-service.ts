import type UserBasicInfoDTO from "~/dtos/UserBasicInfoDTO";
import type { UserDTO } from "~/dtos/UserDTO";
import type UserEditDTO from "~/dtos/UserEditDTO";


const API_URL = "/api/v1/users";
const API_IMAGES_URL = "/api/v1/images";

export async function getUser(id: number) {
  const res = await fetch(`${API_URL}/${id}`);
  if (!res.ok) {
    throw new Error("User not found");
  }
  return await res.json();
}

export async function addUser(
  email: string,
  password: string,
  name: string,
): Promise<UserDTO> {

  const response = await fetch(`${API_URL}/`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ email, encodedPassword: password, name })
  });

  if (!response.ok) {
    throw new Error("Failed to add user");
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
  


