import type ListDTO from "~/dtos/ListDTO";




export async function addList(
  title: string,
  topic: string,
  description: string,
): Promise<ListDTO> {
  const response = await fetch(`${API_URL}/`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      title: title,
      topic: topic,
      description: description,
    }),
  });

  if (!response.ok) {
    throw new Error("Error adding book");
  }

  return await response.json();
}

export async function updateList(
  id: string,
  title: string,
  topic: string,
  description: string,
): Promise<ListDTO> {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ title, topic, description }),
  });

  if (!response.ok) {
    throw new Error("Error updating book");
  }

  return await response.json();
}