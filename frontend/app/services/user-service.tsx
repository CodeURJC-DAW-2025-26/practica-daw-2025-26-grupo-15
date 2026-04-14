import type { UserPostDto } from "~/dtos/UserPostDto";

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
): Promise<UserPostDto> {

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
