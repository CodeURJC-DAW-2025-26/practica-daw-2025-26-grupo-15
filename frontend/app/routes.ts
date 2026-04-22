import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
	layout("routes/home.tsx", [
		index("routes/fyp.tsx"),
		route("login","routes/sign-in.tsx"),
		route("register","routes/sign-up.tsx"),
		route("users/:id", "routes/profile.tsx"),
		route("admin", "routes/admin-panel.tsx"),
		route("lists/:id", "routes/list-view.tsx"),
		route("lists/new", "routes/new-list.tsx"),
		route("lists/edit/:id", "routes/edit-list.tsx"),
		route("follow-requests", "routes/follow-request.tsx"),
		route("followers-following/:type", "routes/followers-following.tsx"),
		route("lists/:listId/exercises/new", "routes/new-exercise.tsx"),
		route("solutions/:id", "routes/solution.tsx"),
		route("exercise/:id", "routes/exercise.tsx")
	]),
] satisfies RouteConfig;
