import type UserBasicInfoDTO from "~/dtos/UserBasicInfoDTO";
import type { UserDTO } from "~/dtos/UserDTO";


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
